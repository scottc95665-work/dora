curl -X PUT "http://localhost:9200/people/person/_mapping" -d '{
"person" : {
  "dynamic" : "strict",
  "properties" : {
    "id" :                  { "type" : "string",  "index" : "not_analyzed" },
    "ssn" :                 { "type" : "integer", "index" : "not_analyzed" },
    "first_name" :          { "type" : "string",  "index" : "not_analyzed" },
    "last_name" :           { "type" : "string",  "index" : "not_analyzed" },
    "gender" :              { "type" : "string",  "index" : "not_analyzed" },
    "birth_date" :          { "type" : "date",    "format": "yyy-MM-dd" },
    "created_at" :          { "type" : "date",    "format": "yyy-MM-dd-HH.mm.ss.SSS" },
    "updated_at" :          { "type" : "date",    "format": "yyy-MM-dd-HH.mm.ss.SSS" }
}}}'
