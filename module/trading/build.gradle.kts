plugins {
    id("module-common")
    id("openapi-generator-adapted")
}

description = "A Java library for the Alpaca brokerage platform Trading API."

openApiGeneratorAdapted {
    environmentUrlProduction = "https://api.alpaca.markets"
    environmentUrlDevelopment = "https://paper-api.alpaca.markets"
}
