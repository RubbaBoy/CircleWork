import React, {Fragment, useEffect, useState} from 'react'
import './CircleStats.scss'
import {Circle, DonationCounts} from "../../../logic/objects";
import {Card} from "react-bootstrap";
import {fetchAuthed} from "../../../logic/request-helper";

export const CircleStats = () => {
    const [circles, setCircles] = useState<Circle[]>([])
    const [donationCount, setDonationCount] = useState<DonationCounts | undefined>()
    const [error, setError] = useState<string | undefined>()

    useEffect(() => {
        fetchAuthed('circles/donation', {method: 'GET'}).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                setError('Donation counts could not be loaded')
                return
            }

            setError(undefined)

            let counts: DonationCounts = json
            console.log('donation counts =');
            console.log(counts);

            setDonationCount(counts)
        })
    }, [])

    return <div className="CircleStats">
        <h5>Circle Stats</h5>
        <Card>
            <Card.Body>
                {error != undefined ? <p className="error">{error}</p> :
                    <Fragment>
                        <h5>Total Donations</h5>
                        <span>${donationCount?.total ?? '-'}</span>

                        <h5>February Donations</h5>
                        <span>${donationCount?.month ?? '-'}</span>
                    </Fragment>
                }
            </Card.Body>
        </Card>
    </div>
}
