#!/bin/bash

# Read the commit message from the file provided by Git
commit_msg=$(cat "$1")

# Define the commit message format regex
# This regex enforces:
# - Starts with type (feat|fix|docs|style|refactor|perf|test|chore)
# - Optional scope in parentheses
# - Colon and space
# - Message content (1-50 characters)
commit_regex='^(feat|fix|docs|style|refactor|perf|test|chore)(\([a-z0-9-]+\))?: .{1,100}$'

echo "Validating commit message format..."
echo "Commit message: '$commit_msg'"
echo "Expected format: type(scope): message"
echo "Example: feat(user): add user registration"

if ! [[ $commit_msg =~ $commit_regex ]]; then
    echo "Error: Invalid commit message format."
    echo "Commit message must follow the format: type(scope): message"
    echo "Allowed types: feat, fix, docs, style, refactor, perf, test, chore"
    echo "Example: feat(user): add user registration"
    exit 1
fi

echo "Commit message format is valid!"
exit 0
