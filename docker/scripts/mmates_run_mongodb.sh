#!/bin/bash
docker run -it -d --name mmates-mongo-container -p 27018:27017 --network mmates --restart always -v mmates_mongodb_data_container:/data/db mongo:latest