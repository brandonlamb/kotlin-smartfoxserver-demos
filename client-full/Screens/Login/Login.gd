extends Node

signal connect_server(host, port, username, password)
signal disconnect_server()

onready var Game = preload("res://Screens/Game/Game.tscn")

onready var _auth_server:Node = $"/root/AuthServer"
onready var _realm_server:Node = $"/root/RealmServer"
onready var _world_server:Node = $"/root/WorldServer"

func _ready():
	# Connect signals to SmartFoxServer
	connect("connect_server", _auth_server, "OnConnect")
	connect("disconnect_server", _auth_server, "OnDisconnect")

	# Connect signals from SmartFoxServer to self
	_auth_server.connect("Login", self, "_on_login")
	_auth_server.connect("LoginError", self, "_on_login_error")
	_auth_server.connect("ConnectionLost", self, "_on_connection_lost")

func _on_login_pressed():
	var host = "127.0.0.1"
	var port = 9933
	var username = $UI/LoginPanel/UserLabel/UserInput.text
	var password = $UI/LoginPanel/PasswordLabel/PasswordInput.text

	$UI/LoginPanel/Login.disabled = true
	$UI/LoginPanel/ServerMessage.text = ""
	emit_signal("connect_server", host, port, username, password)

func _on_quit_pressed():
	$UI/LoginPanel/Login.disabled = false
	$UI/LoginPanel/ServerMessage.text = ""
	emit_signal("disconnect_server")

	get_tree().quit()

func _on_login():
	get_tree().change_scene_to(Game)

func _on_login_error(message):
	$UI/LoginPanel/Login.disabled = false
	$UI/LoginPanel/ServerMessage.text = message

func _on_connection_lost():
	$UI/LoginPanel/Login.disabled = false
	$UI/LoginPanel/ServerMessage.text = "Connection Lost"
