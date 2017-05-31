## Developer micro test of search functionality on empty Elasticsearch

Assuming local Elasticsearch is listening on `localhost:9200` and Dora is listening on `localhost:8080` with swagger UI turned on.

1. Create test index:

```
PUT http://localhost:9200/people
{
    "settings" : {
        "number_of_shards" : 1
    },
    "mappings" : {
            "person" : {
                "properties" : {
                    "name" : { "type" : "string", "index" : "not_analyzed" }
                }
            }
        }
}
```

2. Put test data into index

```
PUT http://localhost:9200/people/person/1
{
  "name": "John"
}
```

3. Test Dora using swagger:

Open http://localhost:8080/swagger#!/dora/searchIndex, fill the form and submit:

```
index: people
type: person
body: {
          "query" : {
              "term" : { "name" : "John" }
          }
      }
```
or, to fetch all data:
```
body: {
    "query" : {
        "match_all" : {}
    }
}
```

4. Check with Elasticsearch directly:

```http://localhost:9200/people/person/_search```

5. Check with some other REST client:

```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ \ 
           "query" : { \ 
               "term" : { "name" : "John" } \ 
           } \ 
       }' 'http://localhost:8080/dora/people/person/_search'
```
