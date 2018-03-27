This container with elasticsearch is built with support of phonetic search and diminutive names search in the "first_name" and "middle_name" fields in the test "people" index.
The "last_name" field is only configured for phonetic search at the moment.

Given the container is up (see the root README.md about it), run the following commands to upload the test policies and indexes:

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
