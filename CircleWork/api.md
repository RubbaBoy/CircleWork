ALL REQUESTS (not login):
    headers: {"token: ""}

ALL GET:
    all request are query 


EACH DAY: run a task to count monthly totals and stuff


## Login

POST /api/auth/login
    request: {"username": "", "password": ""}
    response: {"token": "", "circle_id": ""}

POST /api/auth/register
    request: {"username": "", "password": "", "circle_id": ""} // if circle_id is empty, create a circle
    response: {"token": "", "circle_id": ""} // given vcircle_id or generated

POST /api/auth/logout
    request: {}
    response: {}

## Circles

GET /api/circles/leaderboard/completion //leaderboard ordered by completion ratio
    request: {}
    response: [{"circle_id": 0, "circle_name": "", "color": 0, "total_raised": 0, "attempted": 0, "completed": 0}, ...]

// The following requests use the current Circle as the logged in user

GET /api/circles/leaderboard/donation //leaderboard ordered by total donations
    request: {}
    response: [{"circle_id": 0, "circle_name": "", "color": 0, "total_raised": 0, "attempted": 0, "completed": 0}, ...]

GET /api/circles/users
    request: {}
    response: [{"id": 0, "name": ""}]

GET /api/circles/feed   // return all goals from users in the circle that are public
    request: {}
    response: [{"id": 0, "owner_id": 0, "name": "", "body": "", "category": 0, "approved_count": 0, "accepted": 0}]

GET /api/circles/donation   //get the total donations from this circle
    request: {}
    response: {"total": "", "month": ""}

PUT /api/circles/adduserbalance   //add int cents to the balance of the current user
    request: {"cents": 0}
    response: {}

## Goals

POST /api/goals/add  //add a new new goal 
    request:{"private": boolean, "goal_name": "", "goal_body": "", "category": "", "due_date": "yyyy-MM-dd"}
    response: [{"goal_id: ""}]

GET /api/goals/get    //get a goal
    request: {"goal_id": ""}
    response: {"user_id": "", "is_private": boolean, "name": "", "body": "", "approved": "", "category": 0}

POST /api/goals/approve      // approve someone else's goal
    request: {"goal_id": 0}
    response: [{"approved": 0}]

GET /api/goals/list     // specific user
    request: {}
    response: [{"id": 0, "user_id": "", "private": boolean, "goalname": "", "goalbody": "", "approved": "", "category": 0}, ...]

## Categories

GET /api/category/list    // list actual categories e.g. Excersize, Math, etc.
    request: {}
    response: [{"id": 0, "name": "", "description": "", "color": 0}, ...]

GET /api/category/resources
    request: {"id": 0} // id of the category to get resources of
    response: [{"id": 0, "category_id": 0, "link": "", "type": 0}]     // category_id is from category above

## Charities

GET /api/charities/list
    request: {}
    response: [{"id": 0, "name": "", "total_donated": 0, "month_donated": 0, "url": ""}]

## Donations

GET /api/donations
    request: {}
    response: {"total": 0, "month": 0}

## Payments

POST /api/payment/pay
    request: {"nonce": ""}
    response: {"amount": "0.00"}

GET /api/payment/client_token
    request: {}
    response: {"token": ""}


## Meta
GET /api/meta
    request: {"url": ""}
    response: {"image": "", "description": "", "title": ""}
