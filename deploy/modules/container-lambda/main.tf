
variable "name" {}
variable "image_uri" {}
variable "memory_size" {
  default = 128
}
variable "environment_vars" {
  type = map(string)
}
variable "description" {
  default = null
}
variable "additional_policy_arns" {
  type = list(string)
}

data "aws_iam_policy" "can_log" {
  arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role" "iam_for_lambda" {
  name = var.name

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "can_log" {
  policy_arn = data.aws_iam_policy.can_log.arn
  role       = aws_iam_role.iam_for_lambda.id
}

resource "aws_iam_role_policy_attachment" "additional" {
  # for_each = var.additional_policy_arns
  count = length(var.additional_policy_arns)
  policy_arn = var.additional_policy_arns[count.index]
  role       = aws_iam_role.iam_for_lambda.id
}

resource "aws_lambda_function" "function" {
  function_name = var.name
  description   = var.description
  role          = aws_iam_role.iam_for_lambda.arn
  image_uri     = var.image_uri
  package_type  = "Image"
  memory_size   = var.memory_size
  environment {
    variables = length(var.environment_vars) == 0 ? null : var.environment_vars
  }
}

output "lambda" {
  value = aws_lambda_function.function
}

output "role" {
  value = aws_iam_role.iam_for_lambda
}