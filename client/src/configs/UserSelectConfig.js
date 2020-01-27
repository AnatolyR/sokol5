import axios from 'axios';

export function UserSelectConfig() {
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
            axios.get(`/api/users/search/userByTitle?title=%25${query}%25`).then((res) => {
                const users = res.data._embedded.users;
                callback(users);
            }).catch(() => {
                callback();
            })
        }
    }
}