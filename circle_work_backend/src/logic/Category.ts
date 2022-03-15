import { createContext } from 'react';
import {GoalCategory} from "./objects";
import {fetchAuthed} from "./request-helper";

const CategoriesContext = createContext<GoalCategory[]>([]);
export default CategoriesContext;

export function listCategories(onError?: (message: string | undefined) => void): Promise<GoalCategory[]> {
    return fetchAuthed('category/list', {
        method: 'GET'
    }).then(async res => {
        let json = await res.json()

        if (res.status != 200) {
            console.log('Bad request status ' + res.status)
            console.log(json)
            onError?.('There was an error listing categories')
            return []
        }

        onError?.(undefined)

        let categories: GoalCategory[] = json
        console.log('=-===== categories =');
        console.log(categories);

        return categories
    })
}

export function getCategory(categories: GoalCategory[], id: number): GoalCategory {
    let found = categories.find(category => category.id == id)
    if (found == undefined) {
        console.log('Undefined category ID: ' + id);
        console.log('Available categories:');
        console.log(categories);
        throw 'uh oh'
    }

    return found
}

