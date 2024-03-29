package seed

import "database/sql"

func insertUsers(db *sql.DB) {
	result, err := db.Exec(
		`INSERT INTO honeycombs.users (email, password, nickname)
		VALUES ('user1@mail.com', 'password1', 'user1'), -- id = 1
			('user2@mail.com', 'password2', 'user2'); -- id = 2, user without any games, no FK, can be deleted
	`)
	if err != nil {
		panic(err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil || rowsAffected != 2 {
		panic("Unexpected state of the DB!")
	}
}

func insertGames(db *sql.DB) {
	result, err := db.Exec(
		`INSERT INTO honeycombs.games (created_by)
		VALUES (1);
	`)
	if err != nil {
		panic(err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil || rowsAffected != 1 {
		panic("Unexpected state of the DB!")
	}

}

func InsertAll(db *sql.DB) {
	insertUsers(db)
	insertGames(db)
}
