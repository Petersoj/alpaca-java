./gradlew clean build uploadArchives # uploadArchives MUST be done with a clean build otherwise the checksums of the files will differ
./gradlew closeAndReleaseRepository
