
variable "image_uri" {
  type = string
}

output "function" {
  value = module.function.lambda
}