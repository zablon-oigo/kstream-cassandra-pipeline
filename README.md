
#### Architecture Diagram
<img width="1499" height="449" alt="cassandra(1)" src="https://github.com/user-attachments/assets/44875660-8c84-43ec-8877-fe2595744678" />



Elasticsearch geo_point
Sync location coordinates with  Elasticsearch
```sh
{
  "mappings": {
    "properties": {
      "id": { "type": "keyword" },
      "latitude": { "type": "double" },
      "longitude": { "type": "double" },
      "location": {
        "type": "geo_point"
      }
    }
  }
}
```