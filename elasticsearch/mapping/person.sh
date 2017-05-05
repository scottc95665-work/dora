#!/bin/bash

curl -XPUT "http://localhost:9200/$1/person/_mapping" -d'
{
"person" : {
"dynamic" : "strict",
"properties" : {
"created_at" : {
"type":   "date",
"format": "yyy-MM-dd-HH.mm.ss.SSS"
},
"date_of_birth" : {
"type" : "date",
"format": "yyyy-MM-dd"
},
"first_name" : {
"type" : "string"
},
"gender" : {
"type" : "string"
},
"id" : {
"type" : "string"
},
"last_name" : {
"type" : "string"
},
"ssn" : {
"type" : "string"
},
"updated_at" : {
"type":   "date",
"format": "yyy-MM-dd-HH.mm.ss.SSS"

}
}
}
}'