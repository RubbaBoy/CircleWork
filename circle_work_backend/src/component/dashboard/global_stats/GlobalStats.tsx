import React, {useEffect, useState} from 'react'
import './GlobalStats.scss'
import {Circle, Goal} from "../../../logic/objects";
import {ButtonGroup, Card, ToggleButton} from "react-bootstrap";
import {fetchAuthed} from "../../../logic/request-helper";

export const GlobalStats = () => {
    const [circles, setCircles] = useState<Circle[]>([])
    const [error, setError] = useState<string | undefined>()
    const [displayMonth, _setDisplayMonth] = useState(false)

    function setDisplayMonth(month: boolean) {
        _setDisplayMonth(month)
        updateLeaderboard()
    }

    function updateLeaderboard() {
        fetchAuthed(displayMonth ? 'circles/leaderboard/completion' : 'circles/leaderboard/donation', {method: 'GET'}).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                setError('Statistics could not be loaded')
                return
            }

            setError(undefined)

            let circles: Circle[] = json
            console.log('circles =');
            console.log(circles);

            setCircles(circles)
        })
    }

    useEffect(() => {
        updateLeaderboard()
    }, [])

    return <div className="GlobalStats">
        <div className="title-line">
            <h5>Global Stats</h5>

            <ButtonGroup className="mb-2">
                <ToggleButton
                    key={'month-radio'}
                    id={'month-radio'}
                    type="radio"
                    variant='outline-success'
                    name="radio"
                    value={'month'}
                    checked={displayMonth}
                    onChange={() => setDisplayMonth(true)}
                >
                    Month
                </ToggleButton>
                <ToggleButton
                    key={'total-radio'}
                    id={'total-radio'}
                    type="radio"
                    variant='outline-success'
                    name="radio"
                    value={'total'}
                    checked={!displayMonth}
                    onChange={() => setDisplayMonth(false)}
                >
                    Total
                </ToggleButton>
            </ButtonGroup>
        </div>

        <Card>
            <Card.Body>
                <ul className={"list"}>
                    {error != undefined ? <p className="error">{error}</p> : circles.map((circle, index) => {
                        return <div key={circle.id} className="entry">
                            <span className="place">#{index + 1}</span>
                            <span className="name">{circle.name}</span>
                        </div>
                    })}
                </ul>
            </Card.Body>
        </Card>
    </div>
}
