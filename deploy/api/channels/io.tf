variable "rest_api_id" {
  type = string
}

variable "root_api_resource_id" {
  type = string
}

variable "authorizer_id" {
  type = string
}

variable "aws_region" {
  type = string
}

variable "account_id" {
  type = string
}

variable "image_uri" {
  type = string
}

output "api" {
  value = [aws_api_gateway_integration.proxy, aws_api_gateway_integration.proxy_root]
}