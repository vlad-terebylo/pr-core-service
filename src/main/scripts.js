db.taxRates.insertMany(
    [
        {
            "_id": 1,
            "propertyType": "FLAT",
            "tax": "6.0"
        },
        {
            "_id": 2,
            "propertyType": "HOUSE",
            "tax": "8.0"
        },
        {
            "_id": 3,
            "propertyType": "OFFICE",
            "tax": "13.0"
        }
    ]
)

db.owners.insertMany([
    {
        "_id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "age": 45,
        "familyStatus": "MARRIED",
        "hasChildren": true,
        "email": "john.doe@example.com",
        "phoneNumber": "+1234567890",
        "birthday": ISODate("1979-05-15T00:00:00Z"),
        "taxesDept": "15000.50",
        "properties": [
            {
                "_id": 1,
                "propertyType": "HOUSE",
                "city": "New York",
                "address": "123 Main St",
                "square": 120,
                "numberOfRooms": 5,
                "cost": "250000.00",
                "dateOfBecomingOwner": ISODate("2010-06-01T00:00:00Z"),
                "dateOfBuilding": ISODate("2005-09-15T00:00:00Z"),
                "propertyCondition": "GOOD"
            }
        ]
    }
]);
