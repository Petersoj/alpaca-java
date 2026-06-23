plugins {
    id("module-common")
    id("openapi-generator-adapted")
}

description = "A Java library for the Alpaca brokerage platform Broker API."

dependencies {
    api(project(":module:common"))
}

openApiGeneratorAdapted {
    environmentUrlProduction = "https://broker-api.alpaca.markets"
    environmentUrlSandbox = "https://broker-api.sandbox.alpaca.markets"
}
