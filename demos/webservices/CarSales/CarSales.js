var package = "cars";
var models = {

    Employee: {
        fields: [
            {name: 'firstName',  type: 'string',    required: true},
            {name: 'lastName',   type: 'string',    required: true},
            {name: 'fullName',   type: 'string',    required: true},
            {name: 'contact',    type: 'json'},
            {name: 'info',       type: 'json'}
        ]
    },

    Car: {
        fields: [
            {name: 'make',      type: 'string',     required: true},
            {name: 'model',     type: 'string',     required: true},
            {name: 'year',      type: 'string',     required: true},
            {name: 'vin',       type: 'string'},
            {name: 'mileage',   type: 'decimal'},
            {name: 'price',     type: 'decimal'}
        ]
    },

    Sale: {
        fields: [
            {name: 'car',       type: 'Car',        required: true},
            {name: 'employee',  type: 'Employee',   required: true},
            {name: 'total',     type: 'decimal',    required: true}
        ]
    }
};