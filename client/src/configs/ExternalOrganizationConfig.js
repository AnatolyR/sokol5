import axios from 'axios';

export function ExternalOrganizationConfig() {
    return {
        maxItems: 1,
        //plugins: ['remove_button'],
        valueField: 'id',
        labelField: 'title',
        searchField: 'title',
        preload: true,
        load(query, callback) {
            if (!query.length) {
                return callback();
            }
            axios.get(`/api/contragents/search/userByTitle?title=%25${query}%25`).then((res) => {
                const users = res.data._embedded.contragents;
                callback(users);
            }).catch(() => {
                callback();
            })
        }
    }
}