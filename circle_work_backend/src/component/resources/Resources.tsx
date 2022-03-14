import React, {Fragment, useEffect, useState} from 'react'
import './Resources.scss'
import {Card, Col, Row} from "react-bootstrap";
import {someBody} from "../stuff";
import {navSideButton} from "../home/Home";
import {CategoryResources, GoalCategory} from "../../logic/objects";
import {listCategories} from "../dashboard/Dashboard";
import {useParams} from "react-router";
import {fetchApi, fetchAuthed} from "../../logic/request-helper";
import {convertColor} from "../../logic/utilities";

export const Resources = () => {
    const {id} = useParams<'id'>();
    const [error, setError] = useState<string | undefined>()
    const [category, setCategory] = useState<GoalCategory | undefined>()
    const [resources, setResources] = useState<CategoryResources[]>([])

    // useEffect(() => {
    //     let bruh: CategoryResources[] = []
    //     for (let i = 0; i < 20; i++) {
    //         bruh.push({id: i, category: 0, title: 'Some demo', link: 'https://www.youtube.com/watch?v=cruT5CzXtn8', type: i % 2})
    //     }
    //     setResources(bruh)
    // }, [])

    const [imageCache, setImageCache] = useState<Map<string, any>>(new Map<string, any>())

    function getImage(url: string): Promise<string | undefined> {
        if (imageCache.has(url)) {
            return imageCache.get(url)
        }

        return fetchApi(`meta?url=${encodeURIComponent(url)}`).then(async res => {
            let json = await res.json()

            if (res.status != 200) {
                console.log('Bad request status ' + res.status)
                console.log(json)
                return;
            }

            let image = json['image']
            let description = json['description']
            let title = json['title']

            setImageCache(old => {
                let ne = new Map(old)
                ne.set(url, image)
                return ne
            })

            return image
        })
    }

    useEffect(() => {
        listCategories().then(categories => setCategory(categories.find(cat => `${cat.id}` == id)))
            .then(_ => fetchAuthed(`category/resources?id=${id}`).then(async res => {
                let json = await res.json()

                if (res.status != 200) {
                    console.log('Bad request status ' + res.status)
                    console.log(json)
                    setError('There was an error getting the category')
                    return []
                }

                setError(undefined)

                let foo: CategoryResources[] = json
                console.log('Resources:');
                console.log(foo);

                setResources(foo)
            }))
                // .then(categories => setCategories(categories))
    }, [])

    function displayResource(resource: CategoryResources) {
        getImage(resource.link)
        return <Col xs={3}>
            <Card className="video resource mb-3" onClick={() => window.open(resource.link, '_blank')}>
                <Card.Img variant="top" src={imageCache.get(resource.link)}/>
                <Card.Body>
                    <Card.Title className="m-0">
                        {resource.title}
                    </Card.Title>
                </Card.Body>
            </Card>
        </Col>;
    }

    return (
        <Fragment>
            <div className="side-buttons">
                {navSideButton('Dashboard', '/dashboard', convertColor(category?.color ?? 0))}
            </div>

            <Row className="Goals mx-0">
                {someBody(
                    <div className="sidebar" style={{color: `#${convertColor(category?.color ?? 0) ?? '425dff'}`}}>
                        <div className="bottom-round"></div>
                    </div>,
                    <Fragment>
                        {error != undefined ? <p className="error">{error}</p> :
                            <Row xs={12} className="first-row">
                                <h3>Resources for {category?.name}</h3>
                                <p>Here is some curated content to help you with your goal!</p>

                                <h4>Videos</h4>
                                <Row xs={12} className="videos-row">
                                    {resources.filter(resource => resource.type == 0).map(displayResource)}
                                </Row>

                                <h4 className="mt-3">Links</h4>
                                <Row xs={12} className="links-row">
                                    {resources.filter(resource => resource.type == 1).map(displayResource)}
                                </Row>
                            </Row>
                        }
                    </Fragment>
                )}
            </Row>
        </Fragment>
    )
}
