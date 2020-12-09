
module "function" {
  source = "./../../modules/container-lambda"

  environment_vars = {
    JWKS_ENDPOINT = "https://aws-effect.au.auth0.com/.well-known/jwks.json"
  }

  timeout = 20
  description = "AWS Effect Custom Authorizer"
  image_uri = var.image_uri
  name = "AwsEffectAuthorizer"
  additional_policy_arns = []
}