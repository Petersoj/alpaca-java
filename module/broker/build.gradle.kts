plugins {
    id("module-common")
    id("openapi-generator-adapted")
}

description = "A Java library for the Alpaca brokerage platform Broker API."

openApiGeneratorAdapted {
    environmentUrlProduction = "https://broker-api.alpaca.markets"
    environmentUrlDevelopment = "https://broker-api.sandbox.alpaca.markets"
}
