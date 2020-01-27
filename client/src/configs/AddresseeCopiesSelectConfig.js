import axios from 'axios';

export function AddresseeCopiesSelectConfig() {
    return {
        maxItems: 100,
        plugins: ['restore_on_backspace', 'remove_button'],
        valueField: 'id',
        labelField: 'title',
        searchField: 'title',
        create: false,
        closeAfterSelect: true,
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