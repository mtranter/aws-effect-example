
resource "aws_iam_policy" "can_dynamo" {
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowDynamoAccess",
            "Effect": "Allow",
            "Action": [
                "dynamodb:GetItem",
                "dynamodb:BatchGetItem",
                "dynamodb:Query",
                "dynamodb:PutItem",
                "dynamodb:UpdateItem",
                "dynamodb:DeleteItem",
                "dynamodb:BatchWriteItem"
            ],
            "Resource": [
                "${aws_dynamodb_table.channels.arn}",
                "${aws_dynamodb_table.channels.arn}/*"
            ]
        }
    ]
}
EOF
}

module "function" {
  source = "./../../modules/container-lambda"
  description = "AWS Effect Channels Handler"
  additional_policy_arns = [aws_iam_policy.can_dynamo.arn]
  timeout = 15
  environment_vars = {
    CHANNELS_TABLE_NAME = local.channels_table_name
    CHANNELS_BY_TOPIC_TABLE_NAME = local.channels_by_topic_index_name
  }
  image_uri = var.image_uri
  name = "AwsEffectChannelsHTTP"
}