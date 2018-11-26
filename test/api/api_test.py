import requests

AWS_SERVER = 'http://ec2-13-57-38-126.us-west-1.compute.amazonaws.com:8000'
PET_FEEDERS = '/petfeeders/'
PET_FEEDER = '/petfeeders/1/'

TOKEN = '370d85a677fd646ef17cf6563a247e70f2d1ac25'

HEADERS = {
    'Authorization' : 'Token ' + TOKEN
}

SUCCESS_CODES = [200, 201, 202]

VERBOSE = 0

class TestResult:
    def __init__(self, error = "", message="", status_code=-1):
        self.error = error
        self.message = message
        self.status_code = status_code
        self.success = self.status_code in SUCCESS_CODES

    def __str__(self):
        if self.success:
            return "\n".join(["Success", self.message, str(self.status_code)]) + "\n"
        return "\n".join(["Failed", self.message, self.error, str(self.status_code)]) + "\n"
    
    def __bool__(self):
        return self.success


def test_get(query):
    result = requests.get(query, headers=HEADERS)
    if result.status_code in SUCCESS_CODES:
        return TestResult("Success", result.text, result.status_code)
    return TestResult("Failed", result.text, result.status_code)
    

def test_put(query, params):
    result = requests.put(query, headers=HEADERS, data=params)
    if result.status_code in SUCCESS_CODES:
        return TestResult("Success", result.text, result.status_code)
    return TestResult("Failed", result.text, result.status_code)

def test_post(query, params): 
    result = requests.post(query, headers=HEADERS, data=params)
    if result.status_code in SUCCESS_CODES:
        return TestResult("Success", result.text, result.status_code)
    return TestResult("Failed", result.text, result.status_code)


def perform_test(name, toggled=0):
    if toggled == None or toggled == False:
        return False
    print(name)
    return True

TEST_GET_FEEDERS = 0
TEST_UPDATE_FEEDER = 0
TEST_CREATE_ACCOUNT = 0
TEST_ADD_PET = 0

def main():
    # simple get
    if perform_test("GetFeeders", TEST_GET_FEEDERS):
        res = test_get(AWS_SERVER + PET_FEEDERS)
        print(res)

    # update pet food feeder
    if perform_test("UpdateFeeder", TEST_UPDATE_FEEDER):
        params = {
            "id": 1,
            "serial_id": "321",
            "setting_cup": 3,
            "setting_interval": 1,
            "setting_closure": False,
            "user": 1,
            "food": 1,
            "pet": 1
        }
        res = test_put(AWS_SERVER + PET_FEEDER, params)
        print(res)


    # account creation
    if perform_test("CreateAccount", TEST_CREATE_ACCOUNT):
        params = {
            "username": "test",
            "password": "test",
        }
        res = test_post(AWS_SERVER + "/register/", params)
        print(res)

    # pet addition
    if perform_test("AddPet", TEST_ADD_PET):
        params = {
            "chip_id" : "111",
            "pet_type" : "pettype",
            "pet_breed" : "breed2",
            "name" : "spot",
            "birthday" : "2016-01-01",
            "user" : "2"
        }
        res = test_post(AWS_SERVER + "/pets/", params)
        print(res)


if __name__ == '__main__':
    main()
