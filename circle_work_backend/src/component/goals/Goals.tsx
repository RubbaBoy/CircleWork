import React, {Fragment, useEffect, useState} from 'react'
import './Goals.scss'
import '../dashboard/feed/Feed.scss'
import '../dashboard/Dashboard.scss'
import {Card, Row} from "react-bootstrap";
import {someBody} from "../stuff";
import {navSideButton} from "../home/Home";
import {Goal, GoalCategory} from "../../logic/objects";
import {listCategories} from "../dashboard/Dashboard";
import {convertColor} from "../../logic/utilities";
import {useNavigate} from "react-router";
import {fetchAuthed} from "../../logic/request-helper";

/**
 * Reformats a date from yyyy-mm-dd to mm/dd/yyyy
 * @param dateString The date
 */
function reformatDate(dateString: string): string {
    let date = new Date(Date.parse(dateString))
    return `${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}`
}

export const Goals = () => {
    const navigate = useNavigate()
    // the element being hovered
    const [hovering, setHovering] = useState<Goal | undefined>()
    const [error, setError] = useState<string | undefined>()
    const [categories, setCategories] = useState<GoalCategory[]>([
        // {id: 0, name: 'Exercise', description: 'idk', color: 15569972},
        // {id: 1, name: 'Academic', description: 'idk', color: 10227985},
        // {id: 2, name: 'Mental Health', description: 'idk', color: 6744214},
        // {id: 3, name: 'Nutrition', description: 'idk', color: 15303659},
    ])
    const [goals, setGoals] = useState<Goal[]>([
        // {id: 0, owner_id: 0, goal_name: 'Get a C in calc', goal_body: 'This is a longer body describing how I would love to achieve a C or above in Calculus.', due_date: '2021-03-08', owner_name: 'Adam Yarris', approval_count: 0, categoryId: 0, is_private: false}
    ]);

    useEffect(() => {
        listCategories().then(categories => setCategories(categories))

        fetchAuthed('goals/list', {method: 'GET'}).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                setError('Goals could not be loaded')
                return
            }

            setError(undefined)

            let goals: Goal[] = json
            console.log('goals =');
            console.log(goals);

            setGoals(goals)
        })
    }, [])

    function getCategory(id: number): GoalCategory {
        let found = categories.find(category => category.id == id)
        if (found == undefined) {
            console.log('Undefined category ID: ' + id);
            console.log('Available categories:');
            console.log(categories);
            throw 'uh oh'
        }

        return found
    }

    function navigateCategory(id: number) {
        navigate('/resources/' + id)
    }

    return (
        <Fragment>
            <div className="side-buttons">
                {navSideButton('Dashboard', '/dashboard')}
            </div>

            <Row className="Goals mx-0">
                {someBody(
                    <div className="sidebar upside-down">
                        <div className="bottom-round"></div>
                    </div>,
                    <Fragment>
                        <Row xs={12} className="first-row">
                            <h4>My Goals</h4>
                            {error != undefined ? <p className="error">{error}</p> :
                                <ul className={"feed"}>
                                    {goals.map(goal => {
                                        let category = getCategory(goal.categoryId)
                                        return <Card className={"item"} style={{...(hovering == goal ? {boxShadow: `0 0px 10px #${convertColor(category.color)}`} : null)}} onMouseEnter={() => setHovering(goal)} onMouseLeave={() => setHovering(undefined)} onClick={() => navigateCategory(category.id)}>
                                            <Card.Body className={"body"}>
                                                <div className={"left"}>
                                                    <div className="title-line">
                                                        <span className="title">{goal.goal_name}</span>
                                                        <span className="date">{reformatDate(goal.due_date)}</span>
                                                        <span className="category">{category.name}</span>
                                                    </div>
                                                    <p className="body">{goal.goal_body}</p>
                                                </div>
                                                {goal.approval_count == 0 &&
                                                    <div className={"right"}>
                                                        <svg xmlns="http://www.w3.org/2000/svg" className="approve" height="24px" viewBox="0 0 24 24" width="24px" fill="#000000">
                                                            <path d="M0 0h24v24H0V0z" fill="none"/>
                                                            <path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z"/>
                                                        </svg>
                                                    </div>}
                                            </Card.Body>
                                        </Card>;
                                    })}
                                </ul>}
                        </Row>
                    </Fragment>
                )}
            </Row>
        </Fragment>
    )
}
