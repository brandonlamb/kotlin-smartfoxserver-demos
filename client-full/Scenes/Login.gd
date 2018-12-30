extends Node

signal connect_server(host, port, username, password)
signal disconnect_server()

onready var _sfs:Node = $"/root/SmartFoxServer"
onready var Game = preload("res://Scenes/Game.tscn")

func _ready():
	connect("connect_server", _sfs, "OnConnect")
	connect("disconnect_server", _sfs, "OnDisconnect")
	_sfs.connect("Login", self, "_on_login")
	_sfs.connect("LoginError", self, "_on_login_error")
	_sfs.connect("ConnectionLost", self, "_on_connection_lost")

func _on_connect_pressed():
	var host = $UI/Panel/HostLabel/HostInput.text
	var port = $UI/Panel/PortLabel/PortInput.text.to_int()
	var username = $UI/Panel/UserLabel/UserInput.text
	var password = $UI/Panel/PasswordLabel/PasswordInput.text

	$UI/Panel/Connect.disabled = true
	$UI/Panel/Disconnect.disabled = false
	$UI/Panel/ServerMessage.text = ""
	emit_signal("connect_server", host, port, username, password)

func _on_disconnect_pressed():
	$UI/Panel/Connect.disabled = false
	$UI/Panel/Disconnect.disabled = true
	$UI/Panel/ServerMessage.text = ""
	emit_signal("disconnect_server")

func _on_login():
	get_tree().change_scene_to(Game)

func _on_login_error(message):
	$UI/Panel/Connect.disabled = false
	$UI/Panel/Disconnect.disabled = true
	$UI/Panel/ServerMessage.text = message

func _on_connection_lost():
	$UI/Panel/Connect.disabled = false
	$UI/Panel/Disconnect.disabled = true
	$UI/Panel/ServerMessage.text = "Connection Lost"
