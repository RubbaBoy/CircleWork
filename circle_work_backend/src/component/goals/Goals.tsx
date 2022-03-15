import React, {Fragment, useContext, useEffect, useState} from 'react'
import './Goals.scss'
import '../dashboard/feed/Feed.scss'
import '../dashboard/Dashboard.scss'
import {Card, Row} from "react-bootstrap";
import {someBody} from "../stuff";
import {navSideButton} from "../home/Home";
import {Goal, GoalCategory} from "../../logic/objects";
import {convertColor} from "../../logic/utilities";
import {useNavigate} from "react-router";
import {fetchAuthed} from "../../logic/request-helper";
import CategoriesContext, {getCategory} from "../../logic/Category";
import {ActivityItem} from "../activity_item/ActivityItem";

/**
 * Reformats a date from yyyy-mm-dd to mm/dd/yyyy
 * @param dateString The date
 */
export function reformatDate(dateString: string): string {
    let date = new Date(Date.parse(dateString))
    return `${date.getMonth() + 1}/${date.getDate() + 1}/${date.getFullYear()}`
}

export const Goals = () => {
    const navigate = useNavigate()
    // the element being hovered
    const [hovering, setHovering] = useState<Goal | undefined>()
    const [error, setError] = useState<string | undefined>()
    const [goals, setGoals] = useState<Goal[]>([
        // {id: 0, owner_id: 0, goal_name: 'Get a C in calc', goal_body: 'This is a longer body describing how I would love to achieve a C or above in Calculus.', due_date: '2021-03-08', owner_name: 'Adam Yarris', approval_count: 0, categoryId: 0, is_private: false}
    ]);
    const categories = useContext(CategoriesContext)

    useEffect(() => {
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

    function approve(e: React.MouseEvent<HTMLElement, MouseEvent>, goal: Goal) {
        e.stopPropagation()
        console.log('approve goal ' + goal.id);
        fetchAuthed('goals/approve', {method: 'POST', body: JSON.stringify({
                'goal_id': goal.id,
            })}).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                return
            }

            goal.approval_count = json['approved']
            setGoals(old => old) // Refresh state
        })
    }

    function navigateCategory(e: React.MouseEvent<HTMLElement, MouseEvent>, id: number) {
        navigate('/resources/' + id)
    }

    return (
        <Fragment>
            <div className="side-buttons">
                {navSideButton('Dashboard', '/dashboard')}
            </div>

            <Row className="Goals mx-0">
                {someBody(
                    <div className="sidebar-incomplete upside-down">
                        <div className="bottom-round"></div>
                    </div>,
                    <Fragment>
                        <Row xs={12} className="first-row">
                            <h4>My Goals</h4>
                            {error != undefined ? <p className="error">{error}</p> :
                                <ul className={"feed"}>
                                    {goals.map(goal =>
                                        <ActivityItem goal={goal} title={goal.goal_name} showApprove={goal => goal.approval_count == 0} onApprove={approve} onClick={(e, goal, category) => navigateCategory(e, category.id)}/>)}
                                </ul>}
                        </Row>
                    </Fragment>
                )}
            </Row>
        </Fragment>
    )
}
