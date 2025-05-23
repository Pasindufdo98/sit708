from flask import Flask, request, Response
import requests
import argparse

app = Flask(__name__)

OLLAMA_API_URL = "http://localhost:11434/api/generate"
MODEL = "llama2:latest"  # Change to llama3 if you've downloaded it

def check_ollama_server():
    try:
        response = requests.get("http://localhost:11434")
        if response.status_code == 200:
            print(" Ollama server is running.")
            return True
        else:
            print(" Ollama server is not responding correctly.")
            return False
    except requests.ConnectionError:
        print(" Could not connect to Ollama server. Run it using `ollama serve`.")
        return False

@app.route('/')
def index():
    return "Welcome to the Ollama Chatbot API!"

@app.route('/chat', methods=['POST'])
def chat():
    #  Fix: Parse JSON properly if sent via application/json
    user_message = None

    if request.is_json:
        try:
            user_message = request.get_json().get("userMessage")
        except Exception as e:
            print(f" JSON parse error: {e}")
    else:
        user_message = request.form.get("userMessage") or request.get_data(as_text=True).strip()

    if not user_message:
        return Response("Error: userMessage cannot be empty", status=400, mimetype='text/plain')

    print(f"\n Received userMessage: {user_message}")

    payload = {
        "model": MODEL,
        "prompt": user_message,
        "stream": False,
        "options": {
            "temperature": 0.6,
            "top_p": 0.85,
            "num_predict": 200
        }
    }

    try:
        print("📡 Sending request to Ollama...")
        ollama_response = requests.post(OLLAMA_API_URL, json=payload)
        print(f"Ollama Response Status: {ollama_response.status_code}")
        print(f"Ollama Response Text: {ollama_response.text}")
        ollama_response.raise_for_status()
        result = ollama_response.json()
        raw_output = result.get("response", "").strip()
    except requests.RequestException as e:
        print(f" Error during Ollama API call: {str(e)}")
        raw_output = ""

    if not raw_output or raw_output.isspace():
        raw_output = f"Sorry, I couldn't provide a relevant answer to: '{user_message}'. Please rephrase."

    print(f"🤖 Final Response: {raw_output}\n")
   
    from flask import jsonify
    return jsonify({"reply": raw_output})


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--port', type=int, default=5001, help='Specify the port number')
    args = parser.parse_args()

    if check_ollama_server():
        print(f" App running on port {args.port}")
        app.run(host='0.0.0.0', port=args.port)
    else:
        print(" Exiting due to Ollama server unavailability.")
