package cz.iddqd.smslocationping.model

class DataSource {
    fun loadContacts(): List<Contact> {
        return listOf(
            Contact("Tinky Winky", "123456"),
            Contact("Dipsy", "645321"),
            Contact("Laa-laa", "456789"),
            Contact("Po", "987654"),
            Contact("Noo-noo", "987654321"),
        )
    }
}