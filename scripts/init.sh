#!/usr/bin/env zsh

curl 'http://localhost:8081/orders' -v -H 'Content-Type: application/json' -d '{ "productId": 1, "quantity": 1 }'
curl 'http://localhost:8081/orders' -v -H 'Content-Type: application/json' -d '{ "productId": 3, "quantity": 2 }'
