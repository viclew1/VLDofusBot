if (imgFound("../templates/hunt_frame_top.png")) {
    log("Already an hunt ongoing")
    return
}

reachDestination(-24, -36)

execTimeoutOpe({ clickPoint(1018, 411) }, { imgFound("hunt_tunnel_inside.png") })
execTimeoutOpe({ clickPoint(464, 462) }, { getLocation() == Pair(-25, -36) })
selectHunt()
execTimeoutOpe({ clickPoint(1570, 828) }, { getLocation() == Pair(-24, -36) })
execTimeoutOpe({ clickPoint(1487, 707) }, { imgFound("hunt_home.png") })