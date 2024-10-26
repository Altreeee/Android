import requests
import json

# 服务器的IP地址和端口
base_url = "http://192.168.0.227:55722"

# 测试 / 根路径
def test_root():
    url = f"{base_url}/"
    response = requests.get(url)
    print(f"Response from /: {response.json()}")

# 测试 GET 请求 /demo/
def test_get_demo(a, b):
    url = f"{base_url}/demo/?a={a}&b={b}"
    response = requests.get(url)
    print(f"Response from /demo/: {response.json()}")

# 测试 POST 请求 /demo/
def test_post_demo(a, b):
    url = f"{base_url}/demo/"
    headers = {"Content-Type": "application/json"}
    data = {"a": a, "b": b}
    response = requests.post(url, data=json.dumps(data), headers=headers)
    print(f"Response from /demo/ (POST): {response.json()}")

# 测试 GET 请求 /get_chatrooms/
def test_get_chatrooms():
    url = f"{base_url}/get_chatrooms/"
    response = requests.get(url)
    print(f"Response from /get_chatrooms/: {response.json()}")

# 测试 GET 请求 /get_messages/
def test_get_messages(chatroom_id):
    url = f"{base_url}/get_messages/?chatroom_id={chatroom_id}"
    response = requests.get(url)
    print(f"Response from /get_messages/ for chatroom {chatroom_id}: {response.json()}")

# 测试 POST 请求 /send_message/
def test_send_message(chatroom_id, user_id, name, message):
    url = f"{base_url}/send_message/"
    headers = {"Content-Type": "application/json"}
    data = {
        "chatroom_id": chatroom_id,
        "user_id": user_id,
        "name": name,
        "message": message
    }
    response = requests.post(url, data=json.dumps(data), headers=headers)
    print(f"Response from /send_message/: {response.json()}")

if __name__ == "__main__":
    # 测试不同的 API
    test_root()
    test_get_demo(5, 7)
    test_post_demo(4, 6)
    test_get_chatrooms()
    test_get_messages(3)
    test_send_message(3, "1155218605", "Danny", "Hello World")
