extends Node2D
class_name Entity

var xspeed = 0.0
var yspeed = 0.0
var hp = 10.0

func _process(delta):
    position.x += xspeed * delta
    position.y += yspeed * delta