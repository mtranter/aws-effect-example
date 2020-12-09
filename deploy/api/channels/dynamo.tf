locals {
  channels_table_name = "Channels"
  channels_by_topic_index_name = "ChannesByTopic"
}

resource "aws_dynamodb_table" "channels" {
  hash_key = "id"
  name     = local.channels_table_name
  attribute {
    name = "id"
    type = "S"
  }
  attribute {
    name = "topic"
    type = "S"
  }
  attribute {
    name = "subscriberCount"
    type = "N"
  }
  
  read_capacity = 1
  write_capacity = 1

  global_secondary_index {
    hash_key        = "topic"
    range_key       = "subscriberCount"
    name            = local.channels_by_topic_index_name
    projection_type = "ALL"
    read_capacity = 1
    write_capacity = 1
  }

  tags = {
    App = "aws-effect"
  }
}