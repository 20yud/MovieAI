import mysql.connector
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from flask import Flask, jsonify, request

app = Flask(__name__)

server = 'localhost'
database = 'filmdb'
username = 'root'
password = ''

def get_connection():
    try:
        conn = mysql.connector.connect(
            host=server,
            database=database,
            user=username,
            password=password
        )
        if conn.is_connected():
            return conn
    except mysql.connector.Error as e:
        print(f'Error: {e}')
    return None

# Load movies data and calculate TF-IDF similarity
try:
    conn = get_connection()
    if conn:
        query = 'SELECT * FROM movies'
        df_sanpham = pd.read_sql(query, conn)
finally:
    if conn and conn.is_connected():
        conn.close()

features = ['overview', 'popularity']

def combineFeatures(row):
    return str(row['overview']) + " " + str(row['popularity'])

df_sanpham['combinedFeatures'] = df_sanpham.apply(combineFeatures, axis=1)

tf = TfidfVectorizer()
tfMatrix = tf.fit_transform(df_sanpham['combinedFeatures'])

similar = cosine_similarity(tfMatrix)

number = 6  # Number of similar movies to recommend

@app.route('/api/overview-suggest', methods=['GET'])
def get_data():
    result = []
    productid = request.args.get('id')
    productid = int(productid)
    if productid not in df_sanpham['id'].values:
        return jsonify({'error': 'Invalid ID'})
    
    indexproduct = df_sanpham[df_sanpham['id'] == productid].index[0]
    similarProduct = list(enumerate(similar[indexproduct]))

    sortedSimilarProduct = sorted(similarProduct, key=lambda x: x[1], reverse=True)

    for i in range(1, number + 1):
        movie_index = sortedSimilarProduct[i][0]
        movie_details = {
            'id': int(df_sanpham.loc[movie_index, 'id']),
            'originalTitle': df_sanpham.loc[movie_index, 'original_title'],
            'posterPath': df_sanpham.loc[movie_index, 'poster_path']
        }
        result.append(movie_details)

    data = {'recommended_movies': result}
    return jsonify(data)

@app.route('/api/genre-suggest', methods=['GET'])
def suggest_by_genre():
    result = []
    productid = request.args.get('id')
    productid = int(productid)
    
    conn = get_connection()
    if not conn:
        return jsonify({'error': 'Database connection failed'})

    try:
        # Fetch genres for the given movie
        genre_query = '''
            SELECT g.name 
            FROM Genres g
            JOIN movie_genre mg ON g.id = mg.genre_id
            WHERE mg.movie_id = %s
        '''
        genres = pd.read_sql(genre_query, conn, params=(productid,))
        if genres.empty:
            return jsonify({'error': 'Invalid ID or no genres found'})

        genre_list = genres['name'].tolist()

        # Find other movies with matching genres
        movie_query = '''
            SELECT m.id, m.original_title, m.poster_path, COUNT(*) AS shared_genres
            FROM Movies m
            JOIN movie_genre mg ON m.id = mg.movie_id
            JOIN Genres g ON g.id = mg.genre_id
            WHERE g.name IN (%s) AND m.id != %s
            GROUP BY m.id, m.original_title, m.poster_path
            ORDER BY shared_genres DESC
            LIMIT 6
        ''' % (', '.join(['%s'] * len(genre_list)), '%s')
        
        params = genre_list + [productid]
        similar_movies = pd.read_sql(movie_query, conn, params=params)

        if similar_movies.empty:
            return jsonify({'message': 'No similar movies found'})

        for _, row in similar_movies.iterrows():
            movie_details = {
                'id': int(row['id']),
                'originalTitle': row['original_title'],
                'posterPath': row['poster_path']
            }
            result.append(movie_details)

        data = {'recommended_movies': result}
        return jsonify(data)
    
    except mysql.connector.Error as e:
        return jsonify({'error': str(e)})
    finally:
        if conn.is_connected():
            conn.close()

if __name__ == '__main__':
    app.run(port=5555)
