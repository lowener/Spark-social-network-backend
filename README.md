# Introduction

You can use both of our project. They are not totally connected
so sbt will give you the choice between the two projects.


# Social network in Spark

social-netword-spark init

set your GCP services and API

install google-cloud-sdk
gcloud init
export GOOGLE_APPLICATION_CREDENTIALS=[credenceFile].json

$ sbt "run publish"
push random items on GCP PubSub and add them to GCS

$ sbt "search [WORD]"
find nb of occurencies in GCS [not working well]


# Sentiment Analysis in Spark-ml

We also added sentiment analysis to our projet.

To use it, decompress the zip file, train it, and run it.

- unzip training.zip
- sbt "run [SENTENCE]"