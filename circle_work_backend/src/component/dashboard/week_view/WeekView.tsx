import React, {Fragment, useEffect, useState} from 'react'
import './WeekView.scss'
import {DonationCounts, Goal} from "../../../logic/objects";
import {Card, Row} from "react-bootstrap";
import {fetchAuthed} from "../../../logic/request-helper";

const days = ['Sunday', 'Monday', 'Tuesday', 'Wednsday', 'Thursday', 'Friday', 'Saturday']

interface DayTime {
    weekday: string // days const above
    date: number // the month date number
}

/**
 * Returns the days of the week from now until 6 days from now
 */
function getDays(): DayTime[] {
    let date = new Date()
    let dayTimes: DayTime[] = []

    for (let i = 0; i < 7; i++) {
        dayTimes.push({weekday: days[date.getDay()], date: date.getDate()})
        date.setDate(date.getDate() + 1)
    }

    return dayTimes
}

export const WeekView = () => {
    const [goals, setGoals] = useState<Map<number, Goal[]>>(new Map())
    const [donationCount, setDonationCount] = useState<DonationCounts | undefined>()
    const [error, setError] = useState<string | undefined>()

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

            let lower = new Date()
            let upper = new Date(new Date().setDate(lower.getDate() + 6))

            // date, goal[]
            let dateMap = new Map<number, Goal[]>()

            for (let goal of goals) {
                let goalDate = new Date(goal.due_date)
                let dateNum = goalDate.getDate()
                if (goalDate >= lower && goalDate <= upper) {
                    if (!dateMap.has(dateNum)) {
                        dateMap.set(dateNum, [])
                    }

                    dateMap.get(dateNum)?.push(goal)
                }
            }

            setGoals(dateMap)
        })
    }, [])

    return <Row xs={12} className="WeekView">
        <h5>March 14-21</h5>
        <Card>
            <Card.Body>
                {error != undefined ? <p className="error">{error}</p> :
                    getDays().map((dayTime, i) => (
                        <Fragment>
                            <div className="day">
                                <h5 className="day-name">{dayTime.weekday}</h5>
                                <ul className="tasks">
                                    {goals.get(dayTime.date)?.map(goal =>
                                        <li className="goal">{goal.goal_name}</li>)}
                                </ul>
                            </div>
                            {i != 6 && <div className="sep"></div>}
                        </Fragment>
                    ))}
            </Card.Body>
        </Card>
    </Row>
}
