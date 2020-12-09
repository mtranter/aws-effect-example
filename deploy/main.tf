

module "api" {
  source             = "./api"
  auth_image_uri     = var.auth_image_uri
  aws_region         = var.aws_region
  channels_image_uri = var.channels_image_uri
}

output "api_url" {
  value = module.api.api_url
}