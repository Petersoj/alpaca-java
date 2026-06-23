plugins {
    id("module-common")
    id("openapi-generator-adapted")
}

description = "A Java library for the Alpaca brokerage platform Market Data API."

dependencies {
    api(project(":module:common"))
}

openApiGeneratorAdapted {
    environmentUrlProduction = "https://data.alpaca.markets"
    environmentUrlDevelopment = "https://data.sandbox.alpaca.markets"
}
