clickChain(listOf("fight/fight.png"), "fight/ready.png")
if (imgFound("fight/creature_mode.png", 0.9)) {
    click("fight/creature_mode.png")
}
sleep(5000)
fight(2, "&é&", "\"\"")