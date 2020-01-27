import axios from 'axios';

export function ExternalOrganizationPersonConfig() {
    return {
        maxItems: 1,
        //plugins: ['remove_button'],
        valueField: 'title',
        labelField: 'title',
        searchField: 'title',
        preload: true,
        create: true,
        load(query, callback) {
            if (!query.length) {
                return callback();
            }
            axios.get(`/api/contragentpersons/search/personByTitle?organizationId=${this.depends.externalOrganization}&title=%25${query}%25`).then((res) => {
                const users = res.data._embedded.contragentPersons;
                callback(users);
            }).catch(() => {
                callback();
            })
        }
    }
}