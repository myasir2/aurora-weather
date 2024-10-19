export const dateToClass = () => (v: string) => {
    console.log("Parsing date")

    return (v ? new Date(v) : v);
}
