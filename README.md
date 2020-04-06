# J.A.R.V.I.S.
Just A Rather Very Intelligent System

## Usage 
All responses will have the form

```json
{
  "data": "Mixed type holding the content of the response",
  "message": "Description of what happened",
  "speech": "Proposition of message to the end user of your app"
}
```


Subsequent response definitions will only detail the expected value of the `data` field


### Check-in
Notify J.A.R.V.I.S. you entered a location

**Definition**
`POST /checkIn`

**Arguments**
- `"location":string` the name of the location you entered. possible values are `home` and `work`

**Response**
- `200 OK` on success