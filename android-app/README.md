
** Issue Detail Page 2 **

Get UserType: SAMPLING|MERCHANDISING
MERCHANDISING:
    if ackAt == null : problem is not acknowledged yet
        if(user.buyers.contains(issue2.dField2)) : If user is concerned to this issue2
            if processingAt > 1 : Both level1 and level2 user can acknowledge
                if user.level == LEVEL1 || user.level == LEVEL2
                    add ackButton
            else : Only level 1 user can acknowledge
                if user.level == LEVEL1
                    add ackButton
    else if fixAt == null: problem is acknowledged but not fixed yet
        if(user.buyers.contains(issue2.dField2)) : If user is concerned to this issue2
            if processingAt > 1 : level2 should fix the issue2
                if user.level == LEVEL2
                    add fixButton
            else : level 1 should fix the issue2
                if user.level == LEVEL1
                    add fixButton


**Notification 2 Page**

Get UserType: SAMPLING|MERCHANDISING
SAMPLING:
    Get all Issue raised by this user
    if fixAt != null
        User X fixed Problem X of TeamY:BuyerZ.
    else if ackAt != null : Issue not fixed yet
        User X acknowleged Problem X of TeamY:BuyerZ.
    else
        Problem X for dField1 Y:Buyer Z was raised by you.
MERCHANDISING:
    Get All issue2s for which user is related to.
    for level3: filter issue2 with processingAt == 3
    for level2: filter issue2 with processingAt >= 2
    for level1: filter issue2 with processingAt >= 1
    for level
        if fixAt != null : problem is fixed
            ProblemX of teamY:BuyerZ is resolved.
        else if ackAt != null : problem is acknowledged
            ProblemX of teamY:BuyerZ is acknowledged by {ackBy == urId? you : ackByUser}.
        else
            ProblemX of teamY:BuyerZ is raised by {raisedByUser}.


** Issue Detail 1 **

if user level  = 0
    if fixAt == null : not Fixed yet
        if ackAt == null : not acknowledged
            add ackbutton
        else
            add Fix button
if user level == 1
    if user is concerned
        get value of seekHelp
        if ackAt == null:   not acknowledged
            add ackbutton
        else if fixAt == null && seekHelp == 0
            add seekHelp button

if user level == 2
    if user is concerned
        if ackAt == null && processingAt > 1
            add ackbutton
        else if seekhelp < 2
            add seekHelp button

**Notification 1 Page**

Get UserType: FACTORY
LEVEL0:
    Get all Issue raised by this user
    if fixAt != null
        User X fixed Problem X of TeamY:BuyerZ.
    else if ackAt != null : Issue not fixed yet
        User X acknowleged Problem X of TeamY:BuyerZ.
    else
        Problem X for dField1 Y:Buyer Z was raised by you.
LEVEL1 | LEVEL2 | LEVEL3:
    Get All issue2s for which user is related to.
    for level3: filter issue2 with processingAt == 3
    for level2: filter issue2 with processingAt >= 2
    for level1: filter issue2 with processingAt >= 1
    for level
        if fixAt != null : problem is fixed
            ProblemX of teamY:BuyerZ is resolved.
        else if ackAt != null : problem is acknowledged
            ProblemX of teamY:BuyerZ is acknowledged by {ackBy == urId? you : ackByUser}.
        else
            ProblemX of teamY:BuyerZ is raised by {raisedByUser}.









