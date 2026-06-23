plugins {
    id("module-common")
    id("openapi-generator-adapted")
}

description = "A Java library for the Alpaca brokerage platform Trading API."

dependencies {
    api(project(":module:common"))
}

openApiGeneratorAdapted {
    environmentUrlProduction = "https://api.alpaca.markets"
    environmentUrlDevelopment = "https://paper-api.alpaca.markets"
}
