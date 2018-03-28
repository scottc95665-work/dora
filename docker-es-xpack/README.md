This docker image with elasticsearch is built with support of phonetic search and diminutive names search in the "first_name" and "middle_name" fields in the test "people" index.
The "last_name" field is only configured for phonetic search at the moment.

Also this image supports auto-complete (index-time search-as-you-type). 

Given the docker container is up (see the root README.md about it), run the following commands to upload the test policies and indexes:

  % ./gradlew dockerPopulateXpackPolicies
   
  % ./gradlew dockerPopulateTestPeople
  
The following are example queries for phonetic and diminutive names search:

Actual First name: Michelle
```
  {
      "query": {
          "match": {
              "first_name.phonetic" : "Meechele"
          }
      }
  }
```

Actual First name: Nathan  
```
{
    "query": {
        "match": {
            "first_name.diminutive" : "nate"
        }
    }
}
```

Actual First name: William
```
{
    "query": {
        "match": {
            "first_name.diminutive" : "bill"
        }
    }
}
```

This more complex example will find Nathan (diminutive search by "nate"), Crystal (auto-complete search by "cry"), Alex (phonetic search by "aleks"), and Alexander (not sure why):
```
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "first_name": {
              "query": "aleks nate cry",
              "boost": 1
            }
          }
        },
        {
          "match": {
            "first_name.diminutive": {
              "query": "aleks nate cry",
              "boost": 10
            }
          }
        },
        {
          "match": {
            "first_name.phonetic": {
              "query": "aleks nate cry",
              "boost": 5
            }
          }
        }
      ]
    }
  }
}
```