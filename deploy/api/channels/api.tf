resource "aws_api_gateway_resource" "proxy" {
  parent_id   = var.root_api_resource_id
  path_part   = "{proxy+}"
  rest_api_id = var.rest_api_id
}

resource "aws_api_gateway_method" "proxy" {
  authorization = "CUSTOM"
  authorizer_id = var.authorizer_id
  http_method   = "ANY"
  resource_id   = aws_api_gateway_resource.proxy.id
  rest_api_id   = var.rest_api_id
}

resource "aws_api_gateway_integration" "proxy" {
  http_method = aws_api_gateway_method.proxy.http_method
  integration_http_method = "POST"
  resource_id = aws_api_gateway_resource.proxy.id
  rest_api_id = var.rest_api_id
  type        = "AWS_PROXY"
  uri = module.function.lambda.invoke_arn
}

resource "aws_api_gateway_method" "proxy_root" {
  authorization = "CUSTOM"
  authorizer_id = var.authorizer_id
  http_method   = "ANY"
  resource_id   = var.root_api_resource_id
  rest_api_id   = var.rest_api_id
}

resource "aws_api_gateway_integration" "proxy_root" {
  http_method = aws_api_gateway_method.proxy_root.http_method
  integration_http_method = "POST"
  resource_id = var.root_api_resource_id
  rest_api_id = var.rest_api_id
  type        = "AWS_PROXY"
  uri = module.function.lambda.invoke_arn
}


module "cors" {
  source  = "mewa/apigateway-cors/aws"
  version = "2.0.0"

  api = var.rest_api_id
  resource = aws_api_gateway_resource.proxy.id

  methods = ["GET", "POST", "PUT", "DELETE"]
}

resource "aws_lambda_permission" "apigw_lambda" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = module.function.lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${var.aws_region}:${var.account_id}:${var.rest_api_id}/*/${aws_api_gateway_method.proxy.http_method}${aws_api_gateway_resource.proxy.path}"
}