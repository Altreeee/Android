# to run the script with FastAPI: fastapi dev main.py
# to run the script with uvicorn with fastapi at port 55722: uvicorn main:app --host 0.0.0.0 --port 55722

# import the Fast API package
from fastapi import FastAPI
from datetime import date
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder
from pydantic import BaseModel
from fastapi import Request
import json

# for testing, you can update this one to your student ID
student_list = ["1155218605"] 

# define a Fast API app
app = FastAPI()

# define a route, binding a function to a URL (e.g. GET method) of the server
@app.get("/")
async def root():
  return {"message": "Hello World"}  # the API returns a JSON response

@app.get("/demo/")
async def get_demo(a: int = 0, b: int = 0, status_code=200):
  sum = a+b
  data = {"sum": sum, "date": date.today()}
  return JSONResponse(content=jsonable_encoder(data))

class DemoItem(BaseModel):
  a: int
  b: int

@app.post("/demo/")
async def post_demo(item: DemoItem):  
    print(item)
    if item.a + item.b == 10:
        data = {"status": "OK"}
        return JSONResponse(content=jsonable_encoder(data))
        
    data = {"status": "ERROR"}
    return JSONResponse(content=jsonable_encoder(data))
  
  
@app.get("/get_chatrooms/")
async def get_chatroom(status_code=200):
    data = {
        "data": [
            {
                "id": 3,
                "name": "Chatroom 2"
            },
            {
                "id": 2,
                "name": "General Chatroom"
            }
        ],
        "status": "OK"
    }
    return JSONResponse(content=jsonable_encoder(data))

@app.get("/get_messages/")
async def get_chatroom(chatroom_id: int = -1, status_code=200):
    if chatroom_id == 3:
        data = {
            "data": {
                "messages": [
                {
                    "message": "5722",
                    "name": "Danny",
                    "message_time": "2024-09-29 19:52",
                    "user_id": 1
                },
                {
                    "message": "distributed system",
                    "name": "Danny",
                    "message_time": "2024-09-29 19:51",
                    "user_id": 1
                },
                {
                    "message": "scalable system",
                    "name": "Danny",
                    "message_time": "2024-09-29 19:50",
                    "user_id": 1
                },
                {
                    "message": "mobile app",
                    "name": "Danny",
                    "message_time": "2024-09-29 19:37",
                    "user_id": 1
                },
                {
                    "message": "test",
                    "name": "Danny",
                    "message_time": "2024-09-29 19:36",
                    "user_id": 1
                }
                ],
            },
            "status": "OK"
        }
        return JSONResponse(content=jsonable_encoder(data))
        
        
    if chatroom_id == 2:
        data = {
            "data": {
                "messages": [
                {
                    "message": "software engineering",
                    "name": "Danny",
                    "message_time": "2024-09-29 19:44",
                    "user_id": 1
                },
                {
                    "message": "testing 123 TESTING 123",
                    "name": "Danny",
                    "message_time": "2024-09-29 19:41",
                    "user_id": 1
                },
                {
                    "message": "agile scrum",
                    "name": "Danny",
                    "message_time": "2024-09-29 19:38",
                    "user_id": 1
                }
                ],
            },
            "status": "OK"
        }
        return JSONResponse(content=jsonable_encoder(data))

    data = {
        "data" : { "messages": [] },
        "status": "ERROR"
    }
    return JSONResponse(content=jsonable_encoder(data))


@app.post("/send_message/")
async def send_message(request: Request):  
    item = await request.json()
    print(request, "\n", item)
    
    list_of_keys = list(item.keys())
    
    if len(list_of_keys) != 4:
        data = {"status": "ERROR"}
        return JSONResponse(content=jsonable_encoder(data))
    
    if "chatroom_id" not in item.keys() or item["chatroom_id"] not in [2, 3, "2", "3"]:
        data = {"status": "ERROR"}
        return JSONResponse(content=jsonable_encoder(data))
    
    if "user_id" not in item.keys() or item["user_id"] not in student_list:
        data = {"status": "ERROR"}
        return JSONResponse(content=jsonable_encoder(data))
        
    if "name" not in item.keys() or len(item["name"]) > 20:
        data = {"status": "ERROR"}
        return JSONResponse(content=jsonable_encoder(data))  

    if "message" not in item.keys() or len(item["message"]) > 20:
        data = {"status": "ERROR"}
        return JSONResponse(content=jsonable_encoder(data)) 
        
    data = {"status": "OK"}
    return JSONResponse(content=jsonable_encoder(data))