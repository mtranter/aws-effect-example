locals {
  aws_region = get_env("TF_VAR_aws_region", "ap-southeast-1")
}


remote_state {
  backend = "s3"
  config  = {
    bucket         = "aws-effect-deploy"
    key            = "terraform.tfstate"
    region         = "ap-southeast-2"
    encrypt        = true
    dynamodb_table = "terraform-lock"
  }
}

inputs = {
  aws_region        = local.aws_region
}
