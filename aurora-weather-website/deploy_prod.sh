#!/bin/bash

# Build and upload to S3
npm run build
aws s3 sync dist/saule-clinician-website s3://saule-clinician-website-spa-bucket-prod

# Create CF invalidation
aws cloudfront create-invalidation --distribution-id E200UIVETEC3ZM --paths "/index.html"
