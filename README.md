# msa-order

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/6455043597634783990037b79e0b7fe9)](https://app.codacy.com/gh/Attacktive/msa-order/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![CodeFactor](https://www.codefactor.io/repository/github/attacktive/msa-order/badge)](https://www.codefactor.io/repository/github/attacktive/msa-order)

It's a minimal example of a [microservice](https://en.wikipedia.org/wiki/Microservices) which manipulates the 'Orders'.

Another microservice about 'Products' can be found [here](https://github.com/Attacktive/msa-product).
The frontend to test the microservice can be found [here](https://github.com/Attacktive/msa-demo).

## What It Does

- gets a list of orders
  - shows the details of the product instead of the product ID only
- finds if a specific product is used in an order
- gets an order by the ID
  - shows the details of the product instead of the product ID only
- places an order with *a single* product
  - an order can't be made with non-existing product
- changes an order
  - an order can't be made with non-existing product
- deletes an order
