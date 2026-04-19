

#### Architecture Diagram

<img width="1461" height="433" alt="Untitled Diagram-Page-8 drawio(1)" src="https://github.com/user-attachments/assets/75b0624d-7ecc-489b-a6d5-b932172b2491" />



##### Elasticsearch geo_point

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
