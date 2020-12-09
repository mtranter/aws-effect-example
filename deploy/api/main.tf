resource "aws_api_gateway_rest_api" "api" {
  name = "AwsEffect"
}

data "aws_caller_identity" "me" {}

module "auth" {
  source             = "./auth"
  image_uri          = var.auth_image_uri
}

resource "aws_api_gateway_resource" "channels" {
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  path_part   = "channels"
  rest_api_id = aws_api_gateway_rest_api.api.id
}

module "channels" {
  source               = "./channels"
  account_id           = data.aws_caller_identity.me.account_id
  authorizer_id        = aws_api_gateway_authorizer.authorizer.id
  aws_region           = var.aws_region
  image_uri            = var.channels_image_uri
  rest_api_id          = aws_api_gateway_rest_api.api.id
  root_api_resource_id = aws_api_gateway_resource.channels.id
}

resource "aws_api_gateway_authorizer" "authorizer" {
  name                   = "AwsEffect"
  rest_api_id            = aws_api_gateway_rest_api.api.id
  authorizer_uri         = module.auth.function.invoke_arn
  authorizer_credentials = aws_iam_role.invocation_role.arn
}


resource "aws_iam_role" "invocation_role" {
  name = "AwsEffectApiGatewayAuthorizerInvoker"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "apigateway.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_role_policy" "invocation_policy" {
  name = "default"
  role = aws_iam_role.invocation_role.id

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "lambda:InvokeFunction",
      "Effect": "Allow",
      "Resource": "${module.auth.function.arn}"
    }
  ]
}
EOF
}

resource "aws_api_gateway_deployment" "v1" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  stage_name  = "v1"

  triggers = {
    redeployment = sha1(join(",", list(
      jsonencode(module.channels.api),
    )))
  }

  lifecycle {
    create_before_destroy = true
  }
}

output api_url {
  value = aws_api_gateway_deployment.v1.invoke_url
}